package hexlet.code.service;

import hexlet.code.dto.StatusDto;
import hexlet.code.model.Status;

import java.util.List;

public interface StatusService {

    List<Status> findAllStatuses();

    Status findStatusById(long id);

    Status createStatus(StatusDto statusDto);

    Status updateStatusById(long id, StatusDto statusDto);

    void deleteStatusById(long id);

}
