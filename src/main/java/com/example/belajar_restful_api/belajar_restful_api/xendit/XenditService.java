package com.example.belajar_restful_api.belajar_restful_api.xendit;

import com.xendit.Xendit;
import com.xendit.exception.XenditException;
import com.xendit.model.Invoice;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class XenditService {


    public Invoice createInvoice(String id, int amount, String payerEmail, String description) throws XenditException {


        Invoice invoice = Invoice.create(
                id,
                amount,
                payerEmail,
                description
        );

        return invoice;

    }

}
