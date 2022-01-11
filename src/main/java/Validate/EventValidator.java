package Validate;

import Domain.Event;
import Domain.Message;
import Exceptions.ValidateException;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Objects;

/**
 * validates an event
 */
public class EventValidator implements Validation<Integer, Event> {
    /**
     * @param event event class variable to be validated
     * @throws ValidateException if the id is not a natural number, the id of the sender/receiver ID
     *                           is not a natural number, the event title is empty,
     *                           the event description is empty, the date is lower than this moment
     */
    @Override
    public void genericValidate(Event event) throws ValidateException {
        String err = "";

        if(event.getId() < 1) {
            err += "Id invalid!\n";
        }
        if(event.getDate() != null) {
            if(event.getDate().compareTo(Timestamp.from(Instant.now())) < 0) {
                err += "The date must be higher than this day!\n";
            }
        }
        if(event.getDescription() != null) {
            if(event.getDescription().length() == 0) {
                err += "The description must not be empty!\n";
            }
        }
        if(event.getTitle() != null) {
            if(event.getTitle().length() == 0) {
                err += "The title must not be empty!\n";
            }
        }

        if(err.length() > 0)
            throw new ValidateException(err);
    }
}
