package io.github.CarolinaCedro.POC01.application.service;

import io.github.CarolinaCedro.POC01.application.dto.request.AddressSaveRequest;
import io.github.CarolinaCedro.POC01.application.dto.response.AddressSaveResponse;
import io.github.CarolinaCedro.POC01.config.modelMapper.ModelMapperConfig;
import io.github.CarolinaCedro.POC01.domain.entities.Address;
import io.github.CarolinaCedro.POC01.infra.repository.AddressRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AddressService {

    private final AddressRepository addressRepository;
    private final ModelMapperConfig modelMapper;


    public List<AddressSaveResponse> findAll() {
        return addressRepository.findAll().stream().map(this::dto).collect(Collectors.toList());
    }

    public Optional<AddressSaveResponse> findById(Long id) {
        return addressRepository.findById(id).map(this::dto);
    }


    public Address save(AddressSaveRequest request) {
        return addressRepository.save(modelMapper.convert().map(request,Address.class));
    }

    @Transactional
    public void deleteById(Long id) {
        Optional<Address> address = addressRepository.findById(id);
        if (address.isPresent()){
            addressRepository.deleteById(id);
        }

    }

    private AddressSaveResponse dto(Address address) {
        return modelMapper.convert().map(address,AddressSaveResponse.class);
    }
}
