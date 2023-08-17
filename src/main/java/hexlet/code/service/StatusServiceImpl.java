package hexlet.code.service;

import hexlet.code.dto.StatusDto;
import hexlet.code.exeptions.StatusNotFoundException;
import hexlet.code.model.Status;
import hexlet.code.repository.StatusRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@AllArgsConstructor
public class StatusServiceImpl implements StatusService {

    private final StatusRepository statusRepository;


    @Override
    public List<Status> findAllStatuses() {
        return statusRepository.findAll();
    }

    @Override
    public Status findStatusById(final long id) {
        return statusRepository.findById(id)
                .orElseThrow(() -> new StatusNotFoundException(String.format("Not found status with 'id': %d", id)));
    }

    @Override
    public Status createStatus(final StatusDto statusDto) {
        final Status status = Status.builder()
                .name(statusDto.name())
                .build();

        return statusRepository.save(status);
    }

    @Override
    public Status updateStatusById(final long id, final StatusDto statusDto) {
        final Status statusToUpdate = statusRepository.findById(id)
                .orElseThrow(() -> new StatusNotFoundException(String.format("Not found status with 'id': %d", id)));
        statusToUpdate.setName(statusDto.name());

        return statusRepository.save(statusToUpdate);
    }

    @Override
    public void deleteStatusById(final long id) {
        statusRepository.deleteById(id);
    }

}
