package uk.gov.dwp.uc.pairtest;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import thirdparty.paymentgateway.TicketPaymentService;
import thirdparty.seatbooking.SeatReservationService;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest.Type;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;

public class TicketServiceImplTest {
    TicketService ticketService = new TicketServiceImpl();
    long accountId = 112345678L;
    TicketTypeRequest[] ticketTypeRequests1 = { new TicketTypeRequest(Type.CHILD, 1),
            new TicketTypeRequest(Type.INFANT, 1) };

    @Test(expected = InvalidPurchaseException.class)
    public void exceptionTesting1() {
        ticketService.purchaseTickets(accountId, ticketTypeRequests1);
    }

    TicketTypeRequest[] ticketTypeRequests2 = { new TicketTypeRequest(Type.CHILD, 1) };

    @Test(expected = InvalidPurchaseException.class)
    public void exceptionTesting2() {
        ticketService.purchaseTickets(accountId , ticketTypeRequests2);
    }

    TicketTypeRequest[] ticketTypeRequests3 = { new TicketTypeRequest(Type.INFANT, 1) };

    @Test(expected = InvalidPurchaseException.class)
    public void exceptionTesting3() {
        ticketService.purchaseTickets(accountId, ticketTypeRequests3);
    }

    TicketTypeRequest[] ticketTypeRequests4 = { new TicketTypeRequest(Type.INFANT, 1) };
    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    @Test
    public void whenExceptionThrown_correctErrorIsDisplayed() {
        exceptionRule.expect(InvalidPurchaseException.class);
        exceptionRule.expectMessage("An adult must be present with child and infants");
        ticketService.purchaseTickets(accountId, ticketTypeRequests4);
    }

    TicketTypeRequest[] ticketTypeRequests5 = { new TicketTypeRequest(Type.ADULT, 25) };
    @Rule
    public ExpectedException exceptionRule2 = ExpectedException.none();

    @Test
    public void whenExceptionThrown_correctErrorIsDisplayed2() {
        exceptionRule.expect(InvalidPurchaseException.class);
        exceptionRule.expectMessage("You can only purchase 20 tickets at a time");
        ticketService.purchaseTickets(accountId, ticketTypeRequests5);
    }

    TicketPaymentService ticketPaymentService = mock(TicketPaymentService.class);
    SeatReservationService seatReservationService = mock(SeatReservationService.class);
    
    int totalCost = 200;
    int noOfSeats = 10;
    TicketTypeRequest[] ticketTypeRequests6 = { new TicketTypeRequest(Type.ADULT, 10) };
    @Test
    public void Successful() {
        ticketService = new TicketServiceImpl(ticketPaymentService, seatReservationService);
        doNothing().when(ticketPaymentService).makePayment(accountId, totalCost);
        doNothing().when(seatReservationService).reserveSeat(accountId, noOfSeats);
        ticketService.purchaseTickets(accountId, ticketTypeRequests6);
        verify(ticketPaymentService, times(1)).makePayment(accountId, totalCost);
        verify(seatReservationService, times(1)).reserveSeat(accountId, noOfSeats);
    }
    TicketTypeRequest[] ticketTypeRequests7 = {
         new TicketTypeRequest(Type.ADULT, 10),
          new TicketTypeRequest(Type.CHILD, 5),
           new TicketTypeRequest(Type.INFANT, 5) };
    int totalCost2 = 250;
    int noOfSeats2 = 15;
    @Test
    public void Successful2() {
        ticketService = new TicketServiceImpl(ticketPaymentService, seatReservationService);
        doNothing().when(ticketPaymentService).makePayment(accountId, totalCost2);
        doNothing().when(seatReservationService).reserveSeat(accountId, noOfSeats2);
        ticketService.purchaseTickets(accountId, ticketTypeRequests7);
        verify(ticketPaymentService, times(1)).makePayment(accountId, totalCost2);
        verify(seatReservationService, times(1)).reserveSeat(accountId, noOfSeats2);
    }
    
}
