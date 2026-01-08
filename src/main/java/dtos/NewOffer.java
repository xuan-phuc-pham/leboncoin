package dtos;

import java.util.List;

public record NewOffer(int rep_id, String name, String description, List<Integer> catagories_ids ) {
}
