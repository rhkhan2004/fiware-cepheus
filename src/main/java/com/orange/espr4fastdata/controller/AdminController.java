package com.orange.espr4fastdata.controller;

import com.orange.espr4fastdata.exception.PersistenceException;
import com.orange.espr4fastdata.persistence.Persistence;
import com.orange.espr4fastdata.cep.ComplexEventProcessor;
import com.orange.espr4fastdata.exception.ConfigurationException;
import com.orange.espr4fastdata.model.cep.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

/**
 * Controller for management of the CEP
 */
@RestController
@RequestMapping("/api/v1")
public class AdminController {

    private static Logger logger = LoggerFactory.getLogger(AdminController.class);

    private final ComplexEventProcessor complexEventProcessor;

    private final Persistence persistence;

    @Autowired
    public AdminController(ComplexEventProcessor complexEventProcessor, Persistence persistence) {
        this.complexEventProcessor = complexEventProcessor;
        this.persistence = persistence;

    }

    @RequestMapping(value = "/config", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> configuration(@RequestBody final Configuration configuration) throws PersistenceException {
        logger.debug("Updating configuration: {}", configuration);

        try {
            complexEventProcessor.setConfiguration(configuration);

            persistence.saveConfiguration(configuration);

        } catch (ConfigurationException e) {
            logger.error("Impossible to set configuration");
        }


        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @ExceptionHandler(ConfigurationException.class)
    @ResponseStatus(value=HttpStatus.BAD_REQUEST,reason="Configuration error")
    public ModelAndView handleError(HttpServletRequest req, Exception exception) {
        logger.error("Request: configuration error", exception);

        ModelAndView mav = new ModelAndView();
        mav.addObject("exception", exception);
        mav.addObject("url", req.getRequestURL());
        mav.setViewName("error");
        return mav;
    }
}
