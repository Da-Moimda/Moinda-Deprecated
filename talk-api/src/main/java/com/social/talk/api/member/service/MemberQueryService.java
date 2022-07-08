package com.social.talk.api.member.service;


import com.social.talk.core.domains.member.dto.MemberDto;
import com.social.talk.core.domains.member.entity.Member;
import com.social.talk.core.domains.member.entity.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberQueryService {

    private final MemberRepository memberRepository;

    public MemberDto getMemberInfo(String email) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("없는 이메일 입니다."));

        return member.bindToMemberDto();
    }

}
