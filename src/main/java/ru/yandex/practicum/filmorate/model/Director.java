package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class Director {

    private Integer id;
    @NotNull
    @NotBlank
    private String name;

    public Map<String, Object> toMap() {
        HashMap<String, Object> map = new HashMap<>();

        map.put("director_name", this.name);
        return map;
    }

    @Override
    public String toString() {
        return "Director{" + "id=" + id + ", name='" + name + '\'' + '}';
    }
}
