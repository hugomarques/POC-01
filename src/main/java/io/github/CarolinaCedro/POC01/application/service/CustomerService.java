package io.github.CarolinaCedro.POC01.application.service;

import io.github.CarolinaCedro.POC01.application.dto.request.CustomerSaveRequest;
import io.github.CarolinaCedro.POC01.application.dto.response.CustomerSaveResponse;
import io.github.CarolinaCedro.POC01.config.modelMapper.ModelMapperConfig;
import io.github.CarolinaCedro.POC01.domain.entities.Address;
import io.github.CarolinaCedro.POC01.domain.entities.Customer;
import io.github.CarolinaCedro.POC01.infra.repository.AddressRepository;
import io.github.CarolinaCedro.POC01.infra.repository.CustomerRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final AddressRepository addressRepository;
    private final AddressService addressService;
    private final ModelMapperConfig mapper;

    public List<Customer> findAll() {
        return customerRepository.findAll();
//        return customerRepository.findAll().stream().map(this::dto).collect(Collectors.toList());
    }

    public Optional<CustomerSaveResponse> findById(Long id) {
        return customerRepository.findById(id).map(this::dto);
    }

    @Transactional
    public Customer create(CustomerSaveRequest request) {
        List<Address> addressList = addressRepository.findAllById(request.getAddress());
        Long idPrincipal = null;
        for (Address ids : addressList
        ) {
            if (ids.isPrincipalAddress()) {

                idPrincipal = ids.getId();
            }
        }

        Optional<Address> principalAddress = addressRepository.findById(idPrincipal);

        if (addressList.size() > 5) {
            throw new RuntimeException("Tamanho excedido");
        } else {

            Customer customer = new Customer(request.getEmail(), addressList, request.getPhone(),
                    request.getCpfOrCnpj(), request.getPjOrPf(), principalAddress.get()
            );

            return customerRepository.save(customer);
        }


    }

    @Transactional
    public Customer changePrincipalAddress(Long id, Customer update) {
        Assert.notNull(id, "Não foi possivel atualizar o registro");
        Optional<Customer> optional = customerRepository.findById(id);
        Optional<Address> addressUpdate = addressRepository.findById(update.getAddressPrincipal().getId());

        if (optional.isPresent()) {
            Customer db = optional.get();
            db.zera();
            db.setAddressPrincipal(addressUpdate.get());
            Address address = addressUpdate.get();
            address.setPrincipalAddress(true);
            customerRepository.save(db);
            return db;
        }
        return null;
    }

    public Optional<Address> getPrincipalAddress(Long id) {
        Optional<Customer> customer = customerRepository.findById(id);
        Long address = customer.get().getAddressPrincipal().getId();
        return addressRepository.findById(address);
    }

    public List<Customer> findCustomarByEmail(String email) {
        return customerRepository.findByEmail("%" + email + "%");
    }

    public CustomerSaveResponse dto(Customer customer) {
        return mapper.convert().map(customer, CustomerSaveResponse.class);
    }


}