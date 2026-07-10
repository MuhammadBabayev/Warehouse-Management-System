package az.microservice.werehouseapplication.controller;

import az.microservice.werehouseapplication.model.dto.request.warehouse.ChangeWarehouseOrganizationDto;
import az.microservice.werehouseapplication.model.dto.request.warehouse.CreateWarehouseDto;
import az.microservice.werehouseapplication.model.dto.request.warehouse.UpdateWarehouseDto;
import az.microservice.werehouseapplication.model.dto.response.warehouse.WarehouseResponseDto;
import az.microservice.werehouseapplication.service.Interface.IWarehouseService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/warehouse")
@RequiredArgsConstructor
public class WarehouseController {

    private final IWarehouseService warehouseService;

    @PostMapping
    @PreAuthorize("hasAuthority('warehouse.create')")
    public ResponseEntity<WarehouseResponseDto> createWarehouse(@RequestBody CreateWarehouseDto dto){
        return ResponseEntity.ok(warehouseService.createWarehouse(dto));
    }

    @GetMapping("/organization/{organizatinId}")
    @PreAuthorize("hasAuthority('warehouse.view')")
    public List<WarehouseResponseDto> getAllWarehouseByOrganizationId(@PathVariable Long organizatinId){
        return warehouseService.getWarehouseByOrganizationId(organizatinId);
    }

    @GetMapping
    @PreAuthorize("hasAuthority('warehouse.view')")
    public List<WarehouseResponseDto> getAllWarehouse(){
        return warehouseService.getAllWarehouse();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('warehouse.view')")
    public WarehouseResponseDto getWarehouseById(@PathVariable Long id){
        return warehouseService.getWarehouseById(id);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('warehouse.update')")
    public ResponseEntity<WarehouseResponseDto> updateWarehouse(
            @PathVariable Long id,
            @RequestBody UpdateWarehouseDto dto){
        return ResponseEntity.ok(warehouseService.updateWarehouse(id,dto));
    }

    @PatchMapping("/{id}/organization")
    @PreAuthorize("hasAuthority('warehouse.update')")
    @Operation(summary = "change warehouse's organization")
    public ResponseEntity<WarehouseResponseDto> changeWarehouseOrganization(@PathVariable Long id,
                                                                            @RequestBody ChangeWarehouseOrganizationDto dto){
        return ResponseEntity.ok(warehouseService.changeOrganization(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('warehouse.delete')")
    public ResponseEntity<String> deactiveWarehouse(@PathVariable Long id){
        warehouseService.deactiveWarehouse(id);
        return ResponseEntity.status(HttpStatus.OK).body("Warehouse deactivated successfully");
    }


}
