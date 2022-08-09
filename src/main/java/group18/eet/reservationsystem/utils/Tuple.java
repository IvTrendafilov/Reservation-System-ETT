package group18.eet.reservationsystem.utils;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Tuple<T, X> {
    private T left;
    private X right;

    public static <T,X> Tuple<T, X> of(T left, X right) {
        return new Tuple<>(left, right);
    }
}
