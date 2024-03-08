package ru.shtykin.antispam.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ru.shtykin.antispam.domain.Repository;

public class RepositoryImpl implements Repository {
    @Override
    public List<String> getNumbers() {

        return Arrays.asList(
                "+79021111111",
                "+79022222222",
                "+79023333333",
                "+79024444444",
                "+79025555555",
                "+79026666666"
        );
    }
}
