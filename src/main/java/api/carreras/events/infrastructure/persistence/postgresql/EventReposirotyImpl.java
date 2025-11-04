package api.carreras.events.infrastructure.persistence.postgresql;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import api.carreras.events.domain.Event;
import api.carreras.events.infrastructure.DomainPersistence;
import api.carreras.shared.domain.Logger;
import api.carreras.shared.domain.exception.RepositoryException;
import api.carreras.shared.infrastructure.persistence.Pagination;

@Component
public class EventReposirotyImpl implements DomainPersistence {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private EventRepository eventRepository;

    @Override
    public Page<Event> findAll(Pageable pageable, Event request) {

        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Event> criteriaQuery = criteriaBuilder.createQuery(Event.class);
        Root<Event> rootEvent = criteriaQuery.from(criteriaQuery.getResultType());

        List<Predicate> conditions = new ArrayList<>();

        if (request.getEventLocation() != null) {
            conditions.add(criteriaBuilder.equal(rootEvent.get("eventLocation"), request.getEventLocation()));
        }

        if (request.getEventDescription() != null) {
            conditions.add(criteriaBuilder.equal(rootEvent.get("eventDescription"), request.getEventDescription()));
        }

        if (request.getEventDate() != null) {
            conditions.add(criteriaBuilder.equal(rootEvent.get("eventDate"), request.getEventDate()));
        }

        criteriaQuery.where(conditions.toArray(new Predicate[conditions.size()]));

        try {

            return new Pagination<Event>(
                entityManager,
                criteriaBuilder,
                criteriaQuery,
                pageable
            ).getPagination();

        } catch (Exception e) {
            Logger.log("Error by: " + e.getMessage());

            return new PageImpl<>(new ArrayList<>());
        }
    }

    @Override
    public Event findByEventId(Long eventId) {

        try {
            return eventRepository.findByEventId(eventId);
        } catch (Exception e) {
            Logger.log(e.getMessage());

            throw new RepositoryException("Internal Server Error");
        }

    }

    @Override
    public Event save(Event event) throws Exception {
        try {

            return eventRepository.save(event);
        } catch (ConstraintViolationException e) {
            Logger.log(e.getMessage());

            // ERROR: duplicate key value violates unique constraint "users_un"
            
            if (e.getMessage().contains("constraint [users_un]")) {
                return null;
            }
        }
        } catch (Exception e) {
            Logger.log(e.getMessage());


            throw new RepositoryException("Internal Server Error");
        }
    }

    @Override
    public void delete(Event event) throws Exception {

        try {
            eventRepository.delete(event);
        } catch (Exception e) {
            Logger.log(e.getMessage());

            throw new RepositoryException("Internal Server Error");
        }


    }

}
