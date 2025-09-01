package be16.ordersystem.odering.repository;

import be16.ordersystem.member.domain.Member;
import be16.ordersystem.odering.domain.Ordering;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderingRepository extends JpaRepository<Ordering, Long> {
    List<Ordering> findAllByMember(Member member);
}
