package com.odde.atddv2.spec;

import com.github.leeonky.jfactory.Spec;
import com.github.leeonky.jfactory.Trait;
import com.odde.atddv2.entity.Order;

import static com.odde.atddv2.entity.Order.OrderStatus.toBeDelivered;

public class Orders {
    public static class 订单 extends Spec<Order> {
        @Override
        public void main() {
            property("id").ignore();
        }

        @Trait
        public void 未发货的() {
            property("status").value(toBeDelivered);
            property("deliverNo").value(null);
            property("deliveredAt").value(null);
        }
    }
}
