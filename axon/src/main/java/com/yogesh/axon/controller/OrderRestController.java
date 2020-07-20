package com.yogesh.axon.controller;

import lombok.RequiredArgsConstructor;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.queryhandling.QueryGateway;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import com.yogesh.axon.coreapi.commands.ConfirmOrderCommand;
import com.yogesh.axon.coreapi.commands.PlaceOrderCommand;
import com.yogesh.axon.coreapi.commands.ShipOrderCommand;
import com.yogesh.axon.coreapi.queries.OrderedProduct;
import com.yogesh.axon.coreapi.queries.FindAllOrderedProductsQuery;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class OrderRestController {
    private final CommandGateway commandGateway;
    private final QueryGateway queryGateway;
    @PostMapping("/ship-order")
    public void shipOrder() {
        String orderId = UUID.randomUUID().toString();
        commandGateway.send(new PlaceOrderCommand(orderId, "Deluxe Chair"));
        commandGateway.send(new ConfirmOrderCommand(orderId));
        commandGateway.send(new ShipOrderCommand(orderId));
    }

    @PostMapping("/ship-unconfirmed-order")
    public void shipUnconfirmedOrder() {
        String orderId = UUID.randomUUID().toString();
        commandGateway.send(new PlaceOrderCommand(orderId, "Deluxe Chair"));
        // This throws an exception, as an Order cannot be shipped if it has not been confirmed yet.
        commandGateway.send(new ShipOrderCommand(orderId));
    }

    @GetMapping("/all-orders")
    public List<OrderedProduct> findAllOrderedProducts() {
        return queryGateway.query(new FindAllOrderedProductsQuery(), ResponseTypes.multipleInstancesOf(OrderedProduct.class))
                .join();
    }

}
