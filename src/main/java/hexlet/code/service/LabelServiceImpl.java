package hexlet.code.service;

import hexlet.code.dto.LabelDto;
import hexlet.code.exeption.LabelNotFoundException;
import hexlet.code.model.Label;
import hexlet.code.repository.LabelRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@AllArgsConstructor
public class LabelServiceImpl implements LabelService {

    private final LabelRepository labelRepository;


    @Override
    public List<Label> findAllLabels() {
        return labelRepository.findAll();
    }

    @Override
    public Label findLabelById(final long id) {
        return labelRepository.findById(id)
                .orElseThrow(() -> new LabelNotFoundException(String.format("Not found label with 'id': %d", id)));
    }

    @Override
    public List<Label> findAllLabelById(List<Long> ids) {
        return labelRepository.findAllById(ids);
    }

    @Override
    public Label createLabel(final LabelDto labelDto) {
        final Label label = Label.builder()
                .name(labelDto.name())
                .build();

        return labelRepository.save(label);
    }

    @Override
    public Label updateLabelById(final long id, final LabelDto labelDto) {
        final Label labelToUpdate = labelRepository.findById(id)
                .orElseThrow(() -> new LabelNotFoundException(String.format("Not found label with 'id': %d", id)));
        labelToUpdate.setName(labelDto.name());

        return labelRepository.save(labelToUpdate);
    }

    @Override
    public void deleteLabelById(final long id) {
        labelRepository.deleteById(id);
    }

}
