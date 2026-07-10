package az.microservice.werehouseapplication.service.Interface;

public interface ITokenBlackListService {
    void blacklist(String token);
    boolean isBlacklisted(String token) ;

    }
