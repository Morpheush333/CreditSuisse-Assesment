package com.mateuszmedon.assignment;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mateuszmedon.assignment.entity.Request;
import com.mateuszmedon.assignment.mapper.RequestMapper;
import com.mateuszmedon.assignment.model.RequestDao;
import com.mateuszmedon.assignment.shared.dto.RequestDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.security.InvalidParameterException;
import java.sql.Connection;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static com.mateuszmedon.assignment.shared.dto.RequestDto.State.STARTED;

@SpringBootApplication
public class AssignmentApplication implements CommandLineRunner {
    private static Logger log = LoggerFactory.getLogger(AssignmentApplication.class);
    private final ObjectMapper objectMapper;
    private final Connection connection;
    private final RequestMapper eventConverter;

    private Map<String, RequestDto> startedMap = new ConcurrentHashMap<>();
    private Map<String, RequestDto> finishedMap = new ConcurrentHashMap<>();

    public static void main(String[] args) {
        log.info("Starting application");
        SpringApplication.run(AssignmentApplication.class, args);
        log.info("Finishing application");
    }

    @Autowired
    public AssignmentApplication(ObjectMapper objectMapper, Connection connection, RequestMapper eventConverter) {
        this.objectMapper = objectMapper;
        this.connection = connection;
        this.eventConverter = eventConverter;
    }

    @Override
    public void run(String... args) throws IOException {
        if (args.length != 1 || args[0].isEmpty()) {
            throw new InvalidParameterException("Please provide a single log file filePath argument");
        }

        String filePath = args[0];

        log.info("Open file {} for processing", filePath);
        try(BufferedReader bufferedReader = new BufferedReader(new FileReader(filePath))) {
            bufferedReader.lines().forEach(this::groupByState);
            processData(startedMap.keySet());

            log.info("Finished processing file {}, closing...", filePath);
        } catch (IOException e) {
            log.error("Failure reading the file, exiting...", e);
            throw e;
        }
    }

    /**
     * Takes event json and converts it to eventDTO object, then based on state saves to start/finish hashmap
     *
     * @param json
     */
    private void groupByState(String json) {
        try {
            log.info("Convert JSON to RequestDto");
            RequestDto eventDTO = Optional.ofNullable(objectMapper.readValue(json, RequestDto.class))
                    .orElseThrow(() -> new NullPointerException("Failed to convert json to EventDTO"));

            log.info("Create STARTED and FINISHED events map");
            if (eventDTO.getState().equals(STARTED)) {
                startedMap.put(eventDTO.getId(), eventDTO);
            } else {
                finishedMap.put(eventDTO.getId(), eventDTO);
            }
        } catch (IOException e) {
            log.error("Failure processing json {}, skipping...", json);
        }
    }

    /**
     * Takes event ids and finds corresponding start and finish events if found saves resulting event and its duration
     *
     * @param ids
     */
    private void processData(Set<String> ids) {
        try(RequestDao eventDao = new RequestDao(connection)) {
            for (String id : ids) {
                RequestDto startEvent = startedMap.get(id);
                RequestDto finishEvent = finishedMap.get(id);
                if (startEvent!= null && finishEvent != null) {
                    log.info("Converting eventDTO to event...");
                    Request event = eventConverter.RequestDTOToRequest(startEvent, finishEvent);

                    log.info("Saving {}", event.toString());
                    eventDao.save(event);
                } else {
                    log.error("Log {} ids is missing start or finish event, skipping...", id);
                }
            }
        }
    }
}
