package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.HashMap;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class Director {

    private Integer id;

    @NotBlank(message = "Имя директора не может быть пустым.")
    @Size(max = 50, message = "Длина имени режисера не должна быть больше 50 символов.")
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
