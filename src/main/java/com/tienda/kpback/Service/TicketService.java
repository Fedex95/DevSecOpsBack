package com.tienda.kpback.Service;

import com.tienda.kpback.Entity.Prestamo;
import com.tienda.kpback.Entity.Ticket;
import com.tienda.kpback.Repository.TicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class TicketService {
    @Autowired
    private TicketRepository ticketRepository;

    public Ticket generateTicket(Prestamo prestamo) {
        Ticket ticket = new Ticket();
        ticket.setPrestamo(prestamo);
        ticket.setCodigo("TICKET-" + UUID.randomUUID().toString().substring(0, 10).toUpperCase());
        ticket.setFechaEmision(java.time.LocalDateTime.now());
        return ticketRepository.save(ticket);
    }
}
