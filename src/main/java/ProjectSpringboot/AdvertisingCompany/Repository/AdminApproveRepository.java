package ProjectSpringboot.AdvertisingCompany.Repository;

import ProjectSpringboot.AdvertisingCompany.Entity.Approve;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AdminApproveRepository extends JpaRepository<Approve, Long> {

    @Query(value = """
        SELECT * FROM approve
        WHERE row_kind = 'HEADER'
        ORDER BY COALESCE(submitted_at, created_at) DESC
        """, nativeQuery = true)
    List<Approve> findAllHeadersOrderByTimeDesc();

    @Query(value = """
        SELECT * FROM approve
        WHERE root_id = :rootId
        ORDER BY COALESCE(seq_no, 0) ASC, id ASC
        """, nativeQuery = true)
    List<Approve> findChildrenByRoot(@Param("rootId") Long rootId);

    @Query(value = """
        SELECT media_type, COUNT(*) FROM approve
        WHERE root_id = :headerId AND row_kind = 'MEDIA'
        GROUP BY media_type
        """, nativeQuery = true)
    List<Object[]> countMediaByType(@Param("headerId") Long headerId);

    @Query(value = """
        SELECT COUNT(*) FROM approve
        WHERE root_id = :headerId AND row_kind = 'ATTACH'
        """, nativeQuery = true)
    long countAttach(@Param("headerId") Long headerId);

    @Query(value = """
        SELECT media_url FROM approve
        WHERE root_id = :headerId
          AND row_kind = 'MEDIA'
          AND media_url IS NOT NULL
        ORDER BY COALESCE(seq_no, 0) ASC, id ASC
        LIMIT 1
        """, nativeQuery = true)
    Optional<String> firstMediaUrl(@Param("headerId") Long headerId);

    @Query(value = """
        SELECT COALESCE(MAX(seq_no), 0) FROM approve
        WHERE root_id = :rootId
        """, nativeQuery = true)
    Integer findMaxSeqNo(@Param("rootId") Long rootId);
}
