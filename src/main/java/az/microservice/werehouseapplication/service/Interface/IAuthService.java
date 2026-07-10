package az.microservice.werehouseapplication.service.Interface;


import az.microservice.werehouseapplication.model.dto.request.authentication.LoginRequestDto;
import az.microservice.werehouseapplication.model.dto.request.authentication.RegisterRequestDto;
import az.microservice.werehouseapplication.model.dto.response.authentication.AuthResponseDto;

public interface IAuthService {
    AuthResponseDto login(LoginRequestDto request);
    AuthResponseDto register(RegisterRequestDto request);
}
