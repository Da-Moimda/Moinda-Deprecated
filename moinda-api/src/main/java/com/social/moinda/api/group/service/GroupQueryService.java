package com.social.moinda.api.group.service;

import com.social.moinda.api.group.exception.NotFoundGroupException;
import com.social.moinda.core.domains.group.dto.GroupDetails;
import com.social.moinda.core.domains.group.dto.GroupDto;
import com.social.moinda.core.domains.group.entity.Group;
import com.social.moinda.core.domains.group.entity.GroupQueryRepository;
import com.social.moinda.core.domains.meeting.dto.MeetingDto;
import com.social.moinda.core.domains.meeting.entity.MeetingQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GroupQueryService {

    private final GroupQueryRepository groupQueryRepository;
    private final MeetingQueryRepository meetingQueryRepository;

    public List<GroupDto> searchGroups() {
        List<GroupDto> dtoList = groupQueryRepository.findGroups();
        return dtoList;
    }

    public List<GroupDto> searchGroups(String search) {
        List<GroupDto> dtoList = groupQueryRepository.findAllByNameContains(search);
        return dtoList;
    }

    public GroupDetails getGroupDetails(Long groupId) {
        Group group = groupQueryRepository.findById(groupId)
                .orElseThrow(NotFoundGroupException::new);
        System.out.println("group ... " +  group);

        List<MeetingDto> meetings = meetingQueryRepository.findMeetingsByGroupId(groupId);
        System.out.println("meeting ... : " + meetings);

        GroupDetails groupDetails = group.bindToGroupDetails(meetings);
        System.out.println("details... : "+  groupDetails);

        return groupDetails;
    }
}
