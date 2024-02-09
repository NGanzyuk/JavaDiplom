package projects.javadiplom.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.multipart.MultipartFile;
import projects.javadiplom.dto.AuthenticationRequest;
import projects.javadiplom.dto.AuthenticationResponse;
import projects.javadiplom.entity.FileEntity;
import projects.javadiplom.exception.RequestException;
import projects.javadiplom.model.UserModel;
import projects.javadiplom.security.utility.TokenUtility;
import projects.javadiplom.service.implementations.FilesServiceImplementation;
import projects.javadiplom.service.implementations.UsersService;

@RestController
@CrossOrigin
@RequiredArgsConstructor

public class FileStorageController {
    private final UsersService userService;

    private final AuthenticationManager authenticationManager;

    private final TokenUtility tokenUtility;

    private final FilesServiceImplementation filesService;
    private String dir;

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public ResponseEntity<?> saveUser(@RequestBody UserModel user) throws Exception {
        return ResponseEntity.ok(userService.save(user));
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public ResponseEntity<?> createAuthenticationToken(@RequestBody AuthenticationRequest authenticationRequest) throws Exception {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authenticationRequest.getLogin(),
                    authenticationRequest.getPassword()));

            String login = authenticationRequest.getLogin();
            final UserDetails userDetails = userService.loadUserByUsername(login);
            dir = login;
            final String token = tokenUtility.generateToken(userDetails);
            AuthenticationResponse authenticationResponse = new AuthenticationResponse(token);

            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.set("auth-token", authenticationResponse.getToken());
            responseHeaders.set("Content-Type", "application/json;charset=UTF-8");

            String body = "{" + "\"auth-token\"" + ":" + "\"" +
                    authenticationResponse + "\"," +
                    "\"email\"" + ":" + "\"" +
                    login + "\"" +
                    "}";

            return new ResponseEntity<String>(body, responseHeaders, HttpStatus.OK);
        } catch (BadCredentialsException e) {
            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.set("Content-Type", "application/json;charset=UTF-8");
            return new ResponseEntity("", responseHeaders, HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value = "/logout", method = RequestMethod.POST)
    public ResponseEntity<?> logOut(@RequestHeader(value = "auth-token") String token) throws Exception {
        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "/file", method = RequestMethod.GET)
    public ResponseEntity<?> downloadFile(@RequestHeader(value = "auth-token") String token,
                                          @RequestParam(value = "filename") String filename) throws RequestException {
        try {

            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.set("auth-token", token);
            responseHeaders.set("Content-Type", "multipart/form-data");

            Resource resource = filesService.loadFile(dir, filename);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\""
                            + resource.getFilename() + "\"")
                    .body(resource);
        } catch (BadCredentialsException e) {
            throw new RequestException(e.getMessage());
        } catch (HttpClientErrorException.Unauthorized e) {
            throw new RequestException(e.getMessage());
        } catch (HttpServerErrorException.InternalServerError e) {
            throw new RequestException(e.getMessage());
        }
    }

    @RequestMapping(value = "/file", method = RequestMethod.POST)
    public ResponseEntity<?> uploadFile(
            @RequestHeader(value = "auth-token") String token,
            @RequestParam(value = "filename") String filename,
            @RequestBody MultipartFile file) throws RequestException {
        try {
            filesService.newFile(dir, file);
            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.set("auth-token", token);
            responseHeaders.set("Content-Type", "multipart/form-data");
            return ResponseEntity.ok().build();
        } catch (BadCredentialsException e) {
            throw new RequestException(e.getMessage());

        } catch (HttpClientErrorException.Unauthorized e) {
            throw new RequestException(e.getMessage());
        }
    }

    @RequestMapping(value = "/file", method = RequestMethod.PUT)
    public ResponseEntity<?> renameFile(
            @RequestHeader(value = "auth-token") String token,
            @RequestParam(value = "filename") String filename,
            @RequestBody FileEntity fileEntity) throws RequestException {
        try {
            filesService.renameFile(dir, filename, fileEntity.get("filename").toString());
            return ResponseEntity.ok().build();
        } catch (BadCredentialsException e) {
            throw new RequestException(e.getMessage());
        } catch (HttpClientErrorException.Unauthorized e) {
            throw new RequestException(e.getMessage());
        } catch (HttpServerErrorException.InternalServerError e) {
            throw new RequestException(e.getMessage());
        }
    }

    @RequestMapping(value = "/file", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteFile(
            @RequestHeader(value = "auth-token") String token,
            @RequestParam(value = "filename") String filename) throws RequestException {
        try {
            filesService.deleteFile(dir, filename);
            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.set("auth-token", token);
            return ResponseEntity.ok().build();
        } catch (BadCredentialsException e) {
            throw new RequestException(e.getMessage());
        } catch (HttpClientErrorException.Unauthorized e) {
            throw new RequestException(e.getMessage());
        } catch (HttpServerErrorException.InternalServerError e) {
            throw new RequestException(e.getMessage());
        }
    }

    @RequestMapping(value = "/filesList", method = RequestMethod.GET)
    public ResponseEntity<?> listFiles(@RequestHeader(value = "auth-token") String token,
                                       @RequestParam(value = "limit") Integer limit) throws Exception, RequestException {
        try {
            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.set("auth-token", token);
            responseHeaders.set("Content-Type", "application/json;charset=UTF-8");
            String body = filesService.list(dir);
            return new ResponseEntity(body.toString(), responseHeaders, HttpStatus.OK);
        } catch (BadCredentialsException e) {
            throw new RequestException(e.getMessage());
        } catch (HttpClientErrorException.Unauthorized e) {
            throw new RequestException(e.getMessage());
        } catch (HttpServerErrorException.InternalServerError e) {
            throw new RequestException(e.getMessage());
        }
    }
}
