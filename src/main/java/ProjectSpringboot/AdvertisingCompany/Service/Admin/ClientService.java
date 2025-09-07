package ProjectSpringboot.AdvertisingCompany.Service.Admin;

import ProjectSpringboot.AdvertisingCompany.Entity.Client;
import ProjectSpringboot.AdvertisingCompany.Repository.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClientService {
    @Autowired
    private ClientRepository clientRepository;

    public List<Client> getAllClient(){
        return clientRepository.findAll();
    }

    public Client getClientById(Long id){
        return clientRepository.findById(id).orElseThrow(() -> new RuntimeException("Client not found with id/ + id"));
    }

    public Client getClientByEmail(String email){
        return clientRepository.getClientByEmail(email);
    }

    public List<Client> getClientByName(String name){
        return clientRepository.getClientByName(name);
    }

    public Client createClient(Client client){
        return clientRepository.save(client);
    }

    public Client updateClient(Long id, Client clientDetails){
        Client client = getClientById(id);
        client.setName(clientDetails.getName());
        client.setEmail(clientDetails.getEmail());
        client.setPhone(clientDetails.getPhone());
        client.setAddress(clientDetails.getAddress());
        client.setCompany(clientDetails.getCompany());
        client.setCreated_at(clientDetails.getCreated_at());
        client.setUpdated_at(clientDetails.getUpdated_at());
        return clientRepository.save(client);
    }

    public void deleteClient(Long id){
        Client client = getClientById(id);
        clientRepository.delete(client);
    }

    public long countClients() {
        return clientRepository.count();
    }
}
