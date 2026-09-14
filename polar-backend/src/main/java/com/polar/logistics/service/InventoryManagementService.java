package com.polar.logistics.service;

import com.polar.logistics.dto.InventoryDtos;
import com.polar.logistics.entity.InventoryItem;
import com.polar.logistics.entity.enums.StationName;
import com.polar.logistics.exception.ResourceNotFoundException;
import com.polar.logistics.exception.ValidationConflictException;
import com.polar.logistics.repository.InventoryItemRepository;
import com.polar.logistics.security.UserPrincipal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class InventoryManagementService {

    private static final Logger log = LoggerFactory.getLogger(InventoryManagementService.class);
    private final InventoryItemRepository inventoryItemRepository;

    public InventoryManagementService(InventoryItemRepository inventoryItemRepository) {
        this.inventoryItemRepository = inventoryItemRepository;
    }

    @Transactional
    public InventoryDtos.InventoryItemDto createInventoryItem(InventoryDtos.CreateInventoryItemRequest request) {
        if (request.getQuantity() < 0) {
            throw new ValidationConflictException("Inventory quantity cannot be negative");
        }

        InventoryItem item = InventoryItem.builder()
                .name(request.getName())
                .category(request.getCategory())
                .station(request.getStation())
                .quantity(request.getQuantity())
                .unit(request.getUnit())
                .reorderThreshold(request.getReorderThreshold())
                .dailyConsumption(request.getDailyConsumption() != null ? request.getDailyConsumption() : 0.0)
                .expiryDate(request.getExpiryDate())
                .lastUpdated(LocalDateTime.now())
                .build();

        return mapToDto(inventoryItemRepository.save(item));
    }

    @Transactional
    public InventoryDtos.InventoryItemDto adjustInventory(UUID id, InventoryDtos.AdjustInventoryRequest request, UserPrincipal currentUser) {
        InventoryItem item = inventoryItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory item not found with ID: " + id));

        int newQuantity = item.getQuantity() + request.getChangeQuantity();
        if (newQuantity < 0) {
            throw new ValidationConflictException(String.format(
                    "Invalid adjustment: Resulting quantity cannot be negative (Current: %d, Change: %d)",
                    item.getQuantity(), request.getChangeQuantity()
            ));
        }

        item.setQuantity(newQuantity);
        item.setLastUpdated(LocalDateTime.now());
        InventoryItem saved = inventoryItemRepository.save(item);

        log.info("Inventory item '{}' adjusted by {} by user '{}'. New quantity: {}. Reason: {}",
                item.getName(), request.getChangeQuantity(),
                currentUser != null ? currentUser.getUsername() : "system",
                newQuantity, request.getReason());

        return mapToDto(saved);
    }

    @Transactional(readOnly = true)
    public List<InventoryDtos.InventoryItemDto> getAllInventory(StationName station) {
        List<InventoryItem> items = station != null ?
                inventoryItemRepository.findByStationSortedByUrgency(station) :
                inventoryItemRepository.findAllSortedByUrgency();

        return items.stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public InventoryDtos.InventoryItemDto getInventoryById(UUID id) {
        InventoryItem item = inventoryItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory item not found with ID: " + id));
        return mapToDto(item);
    }

    @Transactional(readOnly = true)
    public List<InventoryDtos.InventoryAlertDto> getInventoryAlerts() {
        List<InventoryDtos.InventoryAlertDto> alerts = new ArrayList<>();

        // Low stock alerts
        List<InventoryItem> lowStockItems = inventoryItemRepository.findLowStockItems();
        for (InventoryItem item : lowStockItems) {
            boolean critical = item.getQuantity() == 0 ||
                    (item.getDailyConsumption() > 0 && (item.getQuantity() / item.getDailyConsumption()) <= 3);

            alerts.add(InventoryDtos.InventoryAlertDto.builder()
                    .itemId(item.getId())
                    .itemName(item.getName())
                    .category(item.getCategory())
                    .station(item.getStation())
                    .currentQuantity(item.getQuantity())
                    .threshold(item.getReorderThreshold())
                    .alertType(critical ? "CRITICAL_STOCK" : "LOW_STOCK")
                    .message(String.format("Stock alert: '%s' at %s is at %d %s (Threshold: %d)",
                            item.getName(), item.getStation(), item.getQuantity(), item.getUnit(), item.getReorderThreshold()))
                    .build());
        }

        // Expiring within 30 days alerts
        LocalDate thirtyDaysFromNow = LocalDate.now().plusDays(30);
        List<InventoryItem> expiringItems = inventoryItemRepository.findExpiringItems(thirtyDaysFromNow);
        for (InventoryItem item : expiringItems) {
            alerts.add(InventoryDtos.InventoryAlertDto.builder()
                    .itemId(item.getId())
                    .itemName(item.getName())
                    .category(item.getCategory())
                    .station(item.getStation())
                    .currentQuantity(item.getQuantity())
                    .threshold(item.getReorderThreshold())
                    .alertType("EXPIRING_SOON")
                    .message(String.format("Expiry warning: '%s' at %s expires on %s",
                            item.getName(), item.getStation(), item.getExpiryDate()))
                    .build());
        }

        return alerts;
    }

    @Scheduled(cron = "${app.tracking.low-stock-check-cron:0 0 * * * ?}")
    public void runScheduledStockCheck() {
        List<InventoryItem> lowStock = inventoryItemRepository.findLowStockItems();
        if (!lowStock.isEmpty()) {
            log.warn("SCHEDULED INVENTORY ALERT: {} item(s) are below reorder threshold", lowStock.size());
        }
    }

    @Scheduled(cron = "${app.tracking.expiry-check-cron:0 0 0 * * ?}")
    public void runScheduledExpiryCheck() {
        LocalDate thirtyDays = LocalDate.now().plusDays(30);
        List<InventoryItem> expiring = inventoryItemRepository.findExpiringItems(thirtyDays);
        if (!expiring.isEmpty()) {
            log.warn("SCHEDULED EXPIRY ALERT: {} item(s) expire within 30 days", expiring.size());
        }
    }

    @Transactional
    public void deleteInventoryItem(UUID id) {
        if (!inventoryItemRepository.existsById(id)) {
            throw new ResourceNotFoundException("Inventory item not found with ID: " + id);
        }
        inventoryItemRepository.deleteById(id);
    }

    private InventoryDtos.InventoryItemDto mapToDto(InventoryItem i) {
        boolean lowStock = i.getQuantity() <= i.getReorderThreshold();
        Integer daysRemaining = (i.getDailyConsumption() != null && i.getDailyConsumption() > 0) ?
                (int) Math.floor(i.getQuantity() / i.getDailyConsumption()) : null;
        boolean critical = lowStock && (daysRemaining != null && daysRemaining <= 7);

        return InventoryDtos.InventoryItemDto.builder()
                .id(i.getId())
                .name(i.getName())
                .category(i.getCategory())
                .station(i.getStation())
                .quantity(i.getQuantity())
                .unit(i.getUnit())
                .reorderThreshold(i.getReorderThreshold())
                .dailyConsumption(i.getDailyConsumption())
                .expiryDate(i.getExpiryDate())
                .lastUpdated(i.getLastUpdated())
                .lowStock(lowStock)
                .criticalStock(critical)
                .estimatedDaysRemaining(daysRemaining)
                .build();
    }
}
