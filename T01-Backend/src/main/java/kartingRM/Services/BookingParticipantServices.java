package kartingRM.Services;

import kartingRM.Entities.BookingParticipant;
import kartingRM.Repositories.BookingParticipantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class BookingParticipantServices {
    @Autowired
    private BookingParticipantRepository bookingParticipantRepository;

    @Transactional
    public BookingParticipant saveParticipant(BookingParticipant participant) {
        return bookingParticipantRepository.save(participant);
    }

    @Transactional(readOnly = true)
    public List<BookingParticipant> findByBookingId(Long bookingId) {
        return bookingParticipantRepository.findByBookingId(bookingId);
    }

    @Transactional
    public void deleteParticipant(Long id) {
        bookingParticipantRepository.deleteById(id);
    }
}