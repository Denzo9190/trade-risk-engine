package com.denzo.traderisk.execution;

import org.springframework.stereotype.Service;
import java.util.UUID;

@Service
public class SimulatedExchangeAdapter implements ExchangeAdapter {

    @Override
    public OrderResult placeOrder(OrderRequest request) {
        return new OrderResult(
                request.symbol(),
                request.quantity(),          // полностью исполняем
                request.price(),              // по запрошенной цене
                UUID.randomUUID().toString()
        );
    }
}
