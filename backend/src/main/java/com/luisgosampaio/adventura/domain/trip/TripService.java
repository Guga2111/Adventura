package com.luisgosampaio.adventura.domain.trip;

import com.luisgosampaio.adventura.domain.exceptions.GroupNotFoundException;
import com.luisgosampaio.adventura.domain.exceptions.MemberNotFoundException;
import com.luisgosampaio.adventura.domain.exceptions.TripNotFoundException;
import com.luisgosampaio.adventura.domain.exceptions.UnauthorizedGroupActionException;
import com.luisgosampaio.adventura.domain.group.Group;
import com.luisgosampaio.adventura.domain.group.GroupMember;
import com.luisgosampaio.adventura.domain.group.GroupMemberRepository;
import com.luisgosampaio.adventura.domain.group.GroupRepository;
import com.luisgosampaio.adventura.domain.group.GroupRole;
import com.luisgosampaio.adventura.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TripService {

    private final TripRepository tripRepository;
    private final GroupRepository groupRepository;
    private final GroupMemberRepository memberRepository;
    private final UnsplashService unsplashService;

    @Transactional(readOnly = true)
    public Trip getTrip (Long id) {
        return tripRepository.findById(id)
                .orElseThrow(() -> new TripNotFoundException(id));
    }

    @Transactional(readOnly = true)
    public List<Trip> getTripsByGroup (Long groupId) {
        return tripRepository.findByGroupIdOrderByStartDateDesc(groupId);
    }

    @Transactional(readOnly = true)
    public List<Trip> getAllTrips () {
        return tripRepository.findAll();
    }

    @Transactional
    public Trip createTrip (Trip trip, Long groupId, Long userId) {
        SecurityUtils.verifyAuthenticatedUser(userId);

        GroupMember member = memberRepository.findByUserIdAndGroupId(userId, groupId)
                .orElseThrow(() -> new MemberNotFoundException(groupId, userId));

        if (member.getRole() != GroupRole.ADMIN) {
            throw new UnauthorizedGroupActionException(groupId, userId);
        }

        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new GroupNotFoundException(groupId));

        trip.setGroup(group);
        trip.setCountryCodes(CountryCodeMapper.toCodes(trip.getDestinations()));

        if (!trip.getDestinations().isEmpty()) {
            String coverUrl = unsplashService.fetchCoverImageUrl(trip.getDestinations().get(0));
            trip.setCoverImageUrl(coverUrl);
        }

        return tripRepository.save(trip);
    }

    @Transactional
    public Trip updateTrip (TripDTO tripDTO, Long id, Long userId) {
        SecurityUtils.verifyAuthenticatedUser(userId);

        Trip trip = tripRepository.findById(id)
                .orElseThrow(() -> new TripNotFoundException(id));

        Long groupId = trip.getGroup().getId();
        GroupMember member = memberRepository.findByUserIdAndGroupId(userId, groupId)
                .orElseThrow(() -> new MemberNotFoundException(groupId, userId));

        if (member.getRole() != GroupRole.ADMIN) {
            throw new UnauthorizedGroupActionException(groupId, userId);
        }

        if (tripDTO.getDestinations() != null) {
            trip.setDestinations(tripDTO.getDestinations());
            trip.setCountryCodes(CountryCodeMapper.toCodes(tripDTO.getDestinations()));

            if (!tripDTO.getDestinations().isEmpty()) {
                String coverUrl = unsplashService.fetchCoverImageUrl(tripDTO.getDestinations().get(0));
                trip.setCoverImageUrl(coverUrl);
            }
        }
        if (tripDTO.getDescription() != null) trip.setDescription(tripDTO.getDescription());
        if (tripDTO.getStartDate() != null) trip.setStartDate(tripDTO.getStartDate());
        if (tripDTO.getEndDate() != null) trip.setEndDate(tripDTO.getEndDate());
        if (tripDTO.getMainCurrency() != null) trip.setMainCurrency(tripDTO.getMainCurrency());
        if (tripDTO.getTotalBudget() != null) trip.setTotalBudget(tripDTO.getTotalBudget());
        if (tripDTO.getStatus() != null) trip.setStatus(tripDTO.getStatus());

        return tripRepository.save(trip);
    }

    @Transactional
    public void deleteTrip (Long id, Long userId) {
        SecurityUtils.verifyAuthenticatedUser(userId);

        Trip trip = tripRepository.findById(id)
                .orElseThrow(() -> new TripNotFoundException(id));

        Long groupId = trip.getGroup().getId();
        GroupMember member = memberRepository.findByUserIdAndGroupId(userId, groupId)
                .orElseThrow(() -> new MemberNotFoundException(groupId, userId));

        if (member.getRole() != GroupRole.ADMIN) {
            throw new UnauthorizedGroupActionException(groupId, userId);
        }

        tripRepository.delete(trip);
    }
}
