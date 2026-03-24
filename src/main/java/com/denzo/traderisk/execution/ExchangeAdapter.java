package com.denzo.traderisk.execution;

public interface ExchangeAdapter {
    OrderResult placeOrder(OrderRequest request);
}
