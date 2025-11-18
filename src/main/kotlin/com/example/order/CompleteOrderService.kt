package com.example.order

import order.OrderToProcess
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.common.header.Headers
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Service
import java.nio.charset.StandardCharsets

@Service
class CompleteOrderService {
    companion object {
        private const val PROCESSED_ORDERS_TOPIC = "processed-orders"
        private const val CORRELATION_ID = "orderRequestId"
    }

    private val serviceName = this::class.simpleName

    init {
        println("$serviceName started running..")
    }

    @KafkaListener(topics = [PROCESSED_ORDERS_TOPIC])
    fun processOrder(record: ConsumerRecord<String, OrderToProcess>) {
        val orderRequest = record.value()
        val correlationId = extractCorrelationId(record.headers())
        println("[$serviceName] Completed the order received on topic '$PROCESSED_ORDERS_TOPIC' with correlation id '$correlationId' - $orderRequest")
    }

    private fun extractCorrelationId(headers: Headers): Int? {
        return headers.lastHeader(CORRELATION_ID)?.let {
            String(it.value(), StandardCharsets.UTF_8).toInt()
        }
    }
}