package projects.javadiplom.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
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
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;
import projects.javadiplom.dto.AuthenticationRequest;
import projects.javadiplom.dto.AuthenticationResponse;
import projects.javadiplom.entity.FileEntity;
import projects.javadiplom.exception.RequestException;
import projects.javadiplom.model.userModel;
import projects.javadiplom.security.utility.TokenUtility;
import projects.javadiplom.service.implementations.FilesServiceImplementation;
import projects.javadiplom.service.implementations.UsersService;

@RestController
@CrossOrigin
public class FileStorageController {
    @Autowired
    private UsersService userService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private TokenUtility tokenUtility;

    @Autowired
    private FilesServiceImplementation filesService;

    @Autowired
    private static RequestException requestException;
    private String dir;

    @RequestMapping(value = "/signUp", method = RequestMethod.POST)
    public ResponseEntity<?> saveUser(@RequestBody userModel user) throws Exception {
        return ResponseEntity.ok(userService.save(user));
    }

    @RequestMapping(value = "/signIn", method = RequestMethod.POST)
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
                                          @RequestParam(value = "filename") String filename) {
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
            return requestException.getMessage(token, HttpStatus.BAD_REQUEST);
        } catch (HttpClientErrorException.Unauthorized e) {
            return requestException.getMessage(token, HttpStatus.UNAUTHORIZED);
        } catch (HttpServerErrorException.InternalServerError e) {
            return requestException.getMessage(token, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/file", method = RequestMethod.POST)
    public ResponseEntity<?> uploadFile(
            @RequestHeader(value = "auth-token") String token,
            @RequestParam(value = "filename") String filename,
            @RequestBody MultipartFile file) {
        try {
            filesService.newFile(dir, file);

            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.set("auth-token", token);
            responseHeaders.set("Content-Type", "multipart/form-data");
            StringBuilder body = new StringBuilder();
            return new ResponseEntity(body.toString(), responseHeaders, HttpStatus.OK);
        } catch (BadCredentialsException e) {
            return requestException.getMessage(token, HttpStatus.BAD_REQUEST);
        } catch (HttpClientErrorException.Unauthorized e) {
            return requestException.getMessage(token, HttpStatus.UNAUTHORIZED);
        }
    }

    @RequestMapping(value = "/file", method = RequestMethod.PUT)
    public ResponseEntity<?> renameFile(
            @RequestHeader(value = "auth-token") String token,
            @RequestParam(value = "filename") String filename,
            @RequestBody FileEntity fileEntity) {
        try {
            filesService.renameFile(dir, filename, fileEntity.get("filename").toString());
            return ResponseEntity.ok().build();
        } catch (BadCredentialsException e) {
            return requestException.getMessage(token, HttpStatus.BAD_REQUEST);
        } catch (HttpClientErrorException.Unauthorized e) {
            return requestException.getMessage(token, HttpStatus.UNAUTHORIZED);
        } catch (HttpServerErrorException.InternalServerError e) {
            return requestException.getMessage(token, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/file", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteFile(
            @RequestHeader(value = "auth-token") String token,
            @RequestParam(value = "filename") String filename) {
        try {
            filesService.deleteFile(dir, filename);

            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.set("auth-token", token);
            return ResponseEntity.ok().build();
        } catch (BadCredentialsException e) {
            return requestException.getMessage(token, HttpStatus.BAD_REQUEST);
        } catch (HttpClientErrorException.Unauthorized e) {
            return requestException.getMessage(token, HttpStatus.UNAUTHORIZED);
        } catch (HttpServerErrorException.InternalServerError e) {
            return requestException.getMessage(token, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/filesList", method = RequestMethod.GET)
    public ResponseEntity<?> listFiles(@RequestHeader(value = "auth-token") String token,
                                       @RequestParam(value = "limit") Integer limit) throws Exception {

        try {
            return filesService.list(dir, token);
        } catch (BadCredentialsException e) {
            return requestException.getMessage(token, HttpStatus.BAD_REQUEST);
        } catch (HttpClientErrorException.Unauthorized e) {
            return requestException.getMessage(token, HttpStatus.UNAUTHORIZED);
        } catch (HttpServerErrorException.InternalServerError e) {
            return requestException.getMessage(token, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
