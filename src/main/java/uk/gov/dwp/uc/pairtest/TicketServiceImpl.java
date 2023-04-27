package uk.gov.dwp.uc.pairtest;

import java.util.Arrays;

import thirdparty.paymentgateway.TicketPaymentService;
import thirdparty.seatbooking.SeatReservationService;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;

public class TicketServiceImpl implements TicketService {
    /**
     * Should only have private methods other than the one below.
     */
    private TicketPaymentService ticketPaymentService;
    private SeatReservationService seatReservationService;
    private int noOfSeats = 0;
    private int totalCost = 0;
    private int totalTickets = 0;

    TicketServiceImpl()
    {   
    }
    
    TicketServiceImpl(TicketPaymentService ticketPaymentService, SeatReservationService seatReservationService)
    {
        this.ticketPaymentService = ticketPaymentService;
        this.seatReservationService = seatReservationService;
    }

    @Override
    public void purchaseTickets(Long accountId, TicketTypeRequest... ticketTypeRequests)
            throws InvalidPurchaseException {
        
        boolean adultBooked = false;
        adultBooked = isAdultBooked(ticketTypeRequests);
        if(!adultBooked)
            throw new InvalidPurchaseException("An adult must be present with child and infants");

        calculateSeatsCostAndTickets(ticketTypeRequests);
        if(totalTickets>20)
            throw new InvalidPurchaseException("You can only purchase 20 tickets at a time");
            
        ticketPaymentService.makePayment(accountId, totalCost);
        seatReservationService.reserveSeat(accountId, noOfSeats);
    }
    private void calculateSeatsCostAndTickets(TicketTypeRequest... ticketTypeRequests) {
        Arrays.stream(ticketTypeRequests).forEach(
                ticketTypeRequest -> {
                    TicketTypeRequest.Type type = ticketTypeRequest.getTicketType();
                    int noOfTickets = ticketTypeRequest.getNoOfTickets();
                    totalTickets = totalTickets + noOfTickets;
                    updateSeatsAndCost(type, noOfTickets);
                });
    }
    private void updateSeatsAndCost(TicketTypeRequest.Type type, int noOfTickets) {
        if(!type.equals(TicketTypeRequest.Type.INFANT))
        {
            noOfSeats = noOfSeats + noOfTickets;
            if (type.equals(TicketTypeRequest.Type.ADULT)) 
                totalCost = totalCost + noOfTickets * 20;
            else if (type.equals(TicketTypeRequest.Type.CHILD))
                totalCost = totalCost + noOfTickets * 10;
        }
    }
    private boolean isAdultBooked(TicketTypeRequest... ticketTypeRequests) {
        boolean adultBooked;
        adultBooked = Arrays
                .stream(ticketTypeRequests)
                .anyMatch(
                        ticketTypeRequest -> (ticketTypeRequest.getTicketType() == TicketTypeRequest.Type.ADULT));
        return adultBooked;
    }

}
