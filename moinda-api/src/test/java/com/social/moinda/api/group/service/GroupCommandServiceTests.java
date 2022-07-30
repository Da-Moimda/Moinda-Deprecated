package com.social.moinda.api.group.service;


import com.social.moinda.api.group.dto.GroupCreateDto;
import com.social.moinda.api.group.dto.GroupJoinRequest;
import com.social.moinda.api.group.exception.RegisteredGroupNameException;
import com.social.moinda.api.groupmember.exception.AlreadyJoinGroupMemberException;
import com.social.moinda.api.member.exception.NotFoundMemberException;
import com.social.moinda.core.domains.group.entity.Group;
import com.social.moinda.core.domains.group.entity.GroupQueryRepository;
import com.social.moinda.core.domains.group.entity.GroupRepository;
import com.social.moinda.core.domains.groupmember.entity.GroupMember;
import com.social.moinda.core.domains.groupmember.entity.GroupMemberQueryRepository;
import com.social.moinda.core.domains.groupmember.entity.GroupMemberRepository;
import com.social.moinda.core.domains.member.entity.Member;
import com.social.moinda.core.domains.member.entity.MemberQueryRepository;
import com.social.moinda.core.domains.member.entity.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class GroupCommandServiceTests {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private MemberQueryRepository memberQueryRepository;

    @Mock
    private GroupRepository groupRepository;

    @Mock
    private GroupQueryRepository groupQueryRepository;
    @Mock
    private GroupMemberRepository groupMemberRepository;

    @Mock
    private GroupMemberQueryRepository groupMemberQueryRepository;
    @InjectMocks
    private GroupCommandService groupCommandService;

    @DisplayName("그룹 생성하기 - 성공")
    @Test
    void createSuccessTest() {
        // 그룹이름, 소개, 위치, 모임주제, 정원
        GroupCreateDto groupCreateDto = new GroupCreateDto(
                1L,
                "모임입니다!!",
                "소개글입니다.",
                "경기도",
                "부천시",
                "FREE",
                300
        );

        Group group = groupCreateDto.bindToEntity();
        Member member = new Member("dssd@dsds.com","dsds","1212", null);
        GroupMember groupMember = new GroupMember(group, member);

        given(memberRepository.findById(anyLong())).willReturn(Optional.of(member));
        given(groupRepository.existsByName(anyString())).willReturn(false);
        given(groupMemberRepository.save(any(GroupMember.class))).willReturn(groupMember);

        groupCommandService.create(groupCreateDto);

        then(memberRepository).should(times(1)).findById(anyLong());
        then(groupRepository).should(times(1)).existsByName(anyString());
        then(groupMemberRepository).should(times(1)).save(any(GroupMember.class));
        assertThatNoException();
    }

    @DisplayName("그룹 생성 (사용자가 없어서 실패한다.) - 실패")
    @Test
    void createFailTest() {
        // 그룹이름, 소개, 위치, 모임주제, 정원
        GroupCreateDto groupCreateDto = new GroupCreateDto(
                1L,
                "모임입니다!!",
                "소개글입니다.",
                "경기도",
                "부천시",
                "FREE",
                300
        );

        given(memberRepository.findById(anyLong())).willThrow(new NotFoundMemberException());

        assertThatThrownBy(() -> groupCommandService.create(groupCreateDto))
                .isInstanceOf(NotFoundMemberException.class)
                .hasMessageContaining("등록되지 않은 사용자 입니다.");

        then(memberRepository).shouldHaveNoMoreInteractions();
        then(groupRepository).shouldHaveNoMoreInteractions();
    }

    @DisplayName("그룹명이 중복되어 생성에 실패한다. - 실패")
    @Test
    void createFailBecauseOfNameExistTest() {
        // 그룹이름, 소개, 위치, 모임주제, 정원
        GroupCreateDto groupCreateDto = new GroupCreateDto(
                1L,
                "모임입니다!!",
                "소개글입니다.",
                "경기도",
                "부천시",
                "FREE",
                300
        );

        Member member = new Member("dssd@dsds.com","dsds","1212", null);

        given(memberRepository.findById(anyLong())).willReturn(Optional.of(member));
        given(groupRepository.existsByName(anyString()))
                .willThrow(new RegisteredGroupNameException());

        assertThatThrownBy(() -> groupCommandService.create(groupCreateDto))
                .isInstanceOf(RegisteredGroupNameException.class)
                .hasMessageContaining("등록된 그룹명 입니다.");

        then(memberRepository).shouldHaveNoMoreInteractions();
        then(groupRepository).shouldHaveNoMoreInteractions();
    }

    @DisplayName("그룹 해체하기 - 성공")
    @Test
    void removeGroupSuccessTest() {

        Member member = new Member("user1@eamil.com", "user1", "12121212", null);

        doNothing().when(groupRepository).deleteById(anyLong());
        given(memberRepository.findById(anyLong())).willReturn(Optional.of(member));

        groupCommandService.remove(anyLong(), 1L);

        then(memberRepository).should(times(1)).findById(anyLong());
        then(groupRepository).should(times(1)).deleteById(anyLong());
        assertThatNoException();
    }

    @DisplayName("그룹 가입하는 것에 성공한다.")
    @Test
    void joinGroupSuccessTest() {

        Long groupId = 1L;
        Long memberId = 1L;

        GroupJoinRequest groupJoinRequest = new GroupJoinRequest(groupId, memberId);
        Group group = new Group(null, null, null, null, 300);
        Member member = new Member(null, null, null, null);

        given(groupMemberQueryRepository.isJoinedGroupMember(anyLong(), anyLong()))
                .willReturn(false);
        given(groupQueryRepository.findById(anyLong())).willReturn(Optional.of(group));
        given(memberQueryRepository.findById(anyLong())).willReturn(Optional.of(member));

        // TODO : 해당 그룹에 인원체크를 해야한다.
        groupCommandService.joinGroup(groupJoinRequest);

        then(groupMemberQueryRepository).should(times(1))
                .isJoinedGroupMember(anyLong(), anyLong());

        then(groupQueryRepository).should(times(1)).findById(anyLong());
        then(memberQueryRepository).should(times(1)).findById(anyLong());
    }

    @DisplayName("모임에 이미 가입되어 있어서 실패한다.")
    @Test
    void joinGroupFailTest() {
        Long groupId = 1L;
        Long memberId = 1L;

        GroupJoinRequest groupJoinRequest = new GroupJoinRequest(groupId, memberId);
        Group group = new Group(null, null, null, null, 300);
        Member member = new Member(null, null, null, null);

        given(groupMemberQueryRepository.isJoinedGroupMember(anyLong(), anyLong()))
                .willReturn(true);

        assertThatThrownBy(() -> groupCommandService.joinGroup(groupJoinRequest))
                .isInstanceOf(AlreadyJoinGroupMemberException.class)
                .hasMessageContaining("이미 가입한 그룹입니다.");

        then(groupMemberQueryRepository).should(times(1))
                .isJoinedGroupMember(anyLong(), anyLong());
        then(groupQueryRepository).shouldHaveNoInteractions();
        then(memberQueryRepository).shouldHaveNoInteractions();

    }
}
