package com.luisgosampaio.adventura.domain.excursion;

import com.luisgosampaio.adventura.domain.exceptions.ExcursionNotFoundException;
import com.luisgosampaio.adventura.domain.exceptions.MemberNotFoundException;
import com.luisgosampaio.adventura.domain.exceptions.TripNotFoundException;
import com.luisgosampaio.adventura.domain.group.GroupMember;
import com.luisgosampaio.adventura.domain.group.GroupMemberRepository;
import com.luisgosampaio.adventura.domain.trip.Trip;
import com.luisgosampaio.adventura.domain.trip.TripRepository;
import com.luisgosampaio.adventura.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExcursionService {

    private final TripRepository tripRepository;
    private final ExcursionRepository excursionRepository;
    private final GroupMemberRepository groupMemberRepository;

    @Transactional(readOnly = true)
    public List<Excursion> getExcursions(Long tripId) {
        Long userId = SecurityUtils.getAuthenticatedUserId();

        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new TripNotFoundException(tripId));

        Long groupId = trip.getGroup().getId();
        groupMemberRepository.findByUserIdAndGroupId(userId, groupId)
                .orElseThrow(() -> new MemberNotFoundException(groupId, userId));

        return excursionRepository.findByTripIdOrderByStartDateAsc(tripId);
    }

    @Transactional
    public Excursion processExcursionUpdate(Long tripId, ExcursionMessage message, Principal principal) {
        Long userId = SecurityUtils.getUserIdFromPrincipal(principal);

        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new TripNotFoundException(tripId));

        Long groupId = trip.getGroup().getId();
        groupMemberRepository.findByUserIdAndGroupId(userId, groupId)
                .orElseThrow(() -> new MemberNotFoundException(groupId, userId));

        Excursion excursion;

        switch (message.getAction()) {
            case ADD -> {
                excursion = new Excursion();
                excursion.setTrip(trip);
                excursion.setName(message.getName());
                excursion.setPrice(message.getPrice());
                excursion.setStartDate(message.getStartDate());
                excursion.setEndDate(message.getEndDate());
                if (message.getLocationName() != null) excursion.setLocationName(message.getLocationName());
                if (message.getLatitude()     != null) excursion.setLatitude(message.getLatitude());
                if (message.getLongitude()    != null) excursion.setLongitude(message.getLongitude());
                if (message.getPresenceIds() != null && !message.getPresenceIds().isEmpty()) {
                    List<GroupMember> members = groupMemberRepository.findByIdInAndGroupId(message.getPresenceIds(), groupId);
                    if (members.size() != message.getPresenceIds().size()) {
                        throw new IllegalArgumentException("Some presence IDs do not belong to this group");
                    }
                    excursion.setPresence(members);
                }
                excursion = excursionRepository.save(excursion);
                trip.setExcursionCount(excursionRepository.countByTripId(tripId));
                tripRepository.save(trip);
            }
            case UPDATE -> {
                excursion = excursionRepository.findById(message.getExcursionId())
                        .orElseThrow(() -> new ExcursionNotFoundException(message.getExcursionId()));

                if (!excursion.getTrip().getId().equals(tripId)) {
                    throw new ExcursionNotFoundException(message.getExcursionId());
                }

                if (message.getName() != null) excursion.setName(message.getName());
                if (message.getPrice() != null) excursion.setPrice(message.getPrice());
                if (message.getStartDate() != null) excursion.setStartDate(message.getStartDate());
                if (message.getEndDate() != null) excursion.setEndDate(message.getEndDate());
                if (message.getLocationName() != null) excursion.setLocationName(message.getLocationName());
                if (message.getLatitude()     != null) excursion.setLatitude(message.getLatitude());
                if (message.getLongitude()    != null) excursion.setLongitude(message.getLongitude());
                if (message.getPresenceIds() != null) {
                    List<GroupMember> members = groupMemberRepository.findByIdInAndGroupId(message.getPresenceIds(), groupId);
                    if (members.size() != message.getPresenceIds().size()) {
                        throw new IllegalArgumentException("Some presence IDs do not belong to this group");
                    }
                    excursion.setPresence(members);
                }
                excursion = excursionRepository.save(excursion);
            }
            case DELETE -> {
                excursion = excursionRepository.findById(message.getExcursionId())
                        .orElseThrow(() -> new ExcursionNotFoundException(message.getExcursionId()));

                if (!excursion.getTrip().getId().equals(tripId)) {
                    throw new ExcursionNotFoundException(message.getExcursionId());
                }

                excursionRepository.delete(excursion);
                trip.setExcursionCount(excursionRepository.countByTripId(tripId));
                tripRepository.save(trip);
            }
            default -> throw new IllegalArgumentException("Unknown action: " + message.getAction());
        }

        return excursion;
    }
}
