package az.microservice.werehouseapplication.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ExceptionMessage {

    USER_NOT_FOUND("User not found!"),

    ORGANIZATION_NOT_FOUND("Organization not found!"),

    WAREHOUSE_NOT_FOUND("Warehouse not found!"),

    ZONE_NOT_FOUND("Zone not found!"),

    USER_ALREADY_EXIST("User already exist!"),

    INVALID_PASSWORD("Password is incorrect!"),

    PASSWORD_NOT_MATCH("Passwords not match!"),

    BRAND_ALREADY_EXIST("Brand already exist!"),

    BRAND_NOT_FOUND("Brand not found!"),

    CATEGORY_ALREADY_EXIST("Category already exist!"),

    CATEGORY_NOT_FOUND("Category not found!"),

    INTERNAL_SERVER_ERROR("Something went wrong. Please try again later."),

    CHILD_CATEGORY_NOT_FOUND("Child Category not found!"),

    INBOUND_RECEIPT_NOT_FOUND("Inbound receipt not found!"),

    INVENTORY_ALREADY_EXIST("Inventory already exist!"),

    WAREHOUSE_ALREADY_EXIST("Warehouse already exist!"),

    INVENTORY_NOT_FOUND("Inventory not found!"),

    PRODUCT_NOT_FOUND("Product not found!"),

    INSUFFICIENT_STOCK("Insufficient stock!"),

    INVITATION_NOT_FOUND("Invitation not found!"),

    EMAIL_ALREADY_EXIST("Email already exist!"),

    ROLE_NOT_FOUND("Role not found!"),

    PASSWORD_MISMATCH("Password mismatch!"),

    INVITATION_ALREADY_PROCESSED("Invitation already processed!"),

    INVITATION_EXPIRED("The invitation has expired."),

    INVOICE_GENERATION_NOT_ALLOWED("Invoice can only be generated for COMPLETED transfers."),

    DUBLICATE_INVOICE_CHECK("Check already exists for transfer."),

    INVOICE_NOT_FOUND("Invoice not found!"),

    INVENTORY_COUNT_MORE("inventory available quantity must less than product count"),

    INVALID_INVOICE_STATUS_DRAFT("Invoice can only be issued from DRAFT status."),

    INVALID_INVOICE_STATUS_CONFIRMED("Only ISSUED checks can be confirmed."),

    INVALID_INVOICE_STATUS_DISPUTED("Only ISSUED checks can be disputed. "),

    USERNAME_ALREADY_EXIST("Username already exist!"),

    USER_IS_NOT_ADMIN("User does not have admin role"),

    LOCATION_NOT_FOUND("Location not found! "),

    ORGANIZATION_ALREADY_EXIST("An organization with this name already exists"),

    ADMIN_NOT_FOUND("Admin not found!"),

    SUPER_ADMIN_NOT_FOUND("Super Admin not found!"),

    BARCODE_NOT_FOUND("Barcode not found!"),

    PARTNER_ALREADY_EXIST("Partner already exist!"),

    PARTNER_NOT_FOUND("Partner not found!"),

    PERMISSION_NOT_FOUND("Permission not found!"),

    PERMISSION_ALREADY_EXIST("Permission already exist!"),

    LOCATION_ALREADY_EXIST("Location already exist!"),

    PICKING_NOT_FOUND("Picking not found! "),

    PICKING_ITEM_NOT_FOUND("PickingItem not found! "),

    INVALID_PICKING_STATUS_PENDING("Only PENDING pickings can be started."),

    INVALID_ORDER_STATUS_CONFIRMED("Picking can only be created for CONFIRMED orders."),

    INVALID_PICKING_STATUS_IN_PROGRESS("Picking must be IN_PROGRESS to complete items. "),

    ALL_PICKING_ITEMS_MUST_BE_COMPLETED("All picking items must be completed before completing picking"),

    SUPER_ADMIN_CANNOT_CREATE_PURCHASE_ORDER("SUPER_ADMIN cannot create purchase order directly"),

    PURCHASE_ORDER_NOT_FOUND("PurchaseOrder not found! "),

    ITEMS_CAN_ONLY_BE_ADDED_TO_DRAFT_ORDER("Items can only be added to DRAFT orders"),

    ITEMS_CAN_ONLY_BE_REMOVED_FROM_DRAFT_ORDER("Items can only be removed from DRAFT orders"),

    PURCHASE_ORDER_ITEM_NOT_FOUND("PurchaseOrderItem not found!"),

    ONLY_DRAFT_ORDERS_CAN_BE_CONFIRMED("Only DRAFT orders can be confirmed"),

    RECEIVED_ORDERS_CANNOT_BE_CANCELLED("Received orders cannot be cancelled"),

    ROLE_ALREADY_EXIST("Role already exist! "),

    PERMISSION_ALREADY_ASSIGNED_TO_ROLE("Permission already assigned to this role"),

    PERMISSION_NOT_FOUND_IN_THIS_ROLE("Permission not found in this role"),

    SUPER_ADMIN_CANNOT_CREATE_SALES_ORDER("SUPER_ADMIN cannot create sales order directly"),

    CUSTOMER_NOT_FOUND("Customer not found!"),

    SALES_ORDER_NOT_FOUND("SalesOrder not found!"),

    SHIPPED_DELIVERED_ORDERS_CANNOT_CANCELLED("Shipped or delivered orders cannot be cancelled"),

    SALES_ORDER_ITEM_NOT_FOUND("SalesOrderItem not found! "),

    INVALID_PICKING_STATUS_FOR_COMPLETION("Only IN_PROGRESS pickings can be completed. "),

    SHELF_NOT_FOUND("Shelf not found! "),

    ONLY_SHIPPED_SHIPMENTS_CAN_BE_RETURNED("Only SHIPPED shipments can be returned"),

    ONLY_PREPARING_SHIPMENTS_CAN_BE_SHIPPED("Only PREPARING shipments can be shipped"),

    ONLY_SHIPPED_SHIPMENTS_CAN_BE_DELIVERED("Only SHIPPED shipments can be delivered"),

    DRIVER_NOT_FOUND("Driver not found! "),

    SHIPMENT_NOT_FOUND("Shipment not found! "),

    SHIPMENT_CREATION_REQUIRES_COMPLETED_PICKING("Shipment can only be created after picking is completed"),

    SHELF_ALREADY_EXIST("Shelf already exist! "),

    TRANSFER_NOT_FOUND("Transfer not found"),

    TRANSFER_ITEM_NOT_FOUND("Item not found"),

    INVALID_TRANSFER_STATUS("Items can only be added, deleted, updated from PENDING transfers");

    //CATEGORY_ALREADY_EXIST("Category already exist!");

    private final String message;
}
