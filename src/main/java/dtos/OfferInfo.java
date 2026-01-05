package dtos;

import java.time.LocalDateTime;

public record OfferInfo(
        int id,
        int org_id,
        String org_name,
        String res_name,
        String description,
        String name,
        String creation_date,
        String status  ) {
}
