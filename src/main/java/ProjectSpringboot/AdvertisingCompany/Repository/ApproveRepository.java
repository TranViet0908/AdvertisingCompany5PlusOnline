package ProjectSpringboot.AdvertisingCompany.Repository;

import ProjectSpringboot.AdvertisingCompany.Entity.Approve;
import ProjectSpringboot.AdvertisingCompany.Entity.Approve.RowKind;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ApproveRepository extends JpaRepository<Approve, Long> {

    // Lấy toàn bộ header, sắp theo submitted_at rồi created_at giảm dần
    @Query(value = """
        SELECT * FROM approve
        WHERE row_kind = 'HEADER'
        ORDER BY COALESCE(submitted_at, created_at) DESC
        """, nativeQuery = true)
    List<Approve> findAllHeadersOrderByTimeDesc();

    // Lấy toàn bộ child theo root_id, sắp theo seq_no rồi id
    @Query(value = """
        SELECT * FROM approve
        WHERE root_id = :rootId
        ORDER BY COALESCE(seq_no, 0) ASC, id ASC
        """, nativeQuery = true)
    List<Approve> findChildrenByRoot(@Param("rootId") Long rootId);

    // Lấy seq_no lớn nhất của child để thêm dòng mới
    @Query(value = """
        SELECT COALESCE(MAX(seq_no), 0) FROM approve
        WHERE root_id = :rootId
        """, nativeQuery = true)
    Integer findMaxSeqNo(@Param("rootId") Long rootId);

    // Đếm số media theo loại
    @Query(value = """
        SELECT media_type, COUNT(*) FROM approve
        WHERE root_id = :headerId AND row_kind = 'MEDIA'
        GROUP BY media_type
        """, nativeQuery = true)
    List<Object[]> countMediaByType(@Param("headerId") Long headerId);

    // Đếm ATTACH
    @Query(value = """
        SELECT COUNT(*) FROM approve
        WHERE root_id = :headerId AND row_kind = 'ATTACH'
        """, nativeQuery = true)
    long countAttach(@Param("headerId") Long headerId);

    // Lấy media_url đầu tiên làm preview (nếu có)
    @Query(value = """
        SELECT media_url FROM approve
        WHERE root_id = :headerId
          AND row_kind = 'MEDIA'
          AND media_url IS NOT NULL
        ORDER BY COALESCE(seq_no, 0) ASC, id ASC
        LIMIT 1
        """, nativeQuery = true)
    Optional<String> firstMediaUrl(@Param("headerId") Long headerId);
}
