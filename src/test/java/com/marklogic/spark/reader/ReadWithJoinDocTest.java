package com.marklogic.spark.reader;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.marklogic.spark.AbstractIntegrationTest;
import com.marklogic.spark.Options;
import org.apache.spark.sql.Row;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ReadWithJoinDocTest extends AbstractIntegrationTest {

    @Test
    void jsonDocuments() throws Exception {
        List<Row> rows = newDefaultReader()
            .option(Options.READ_OPTIC_DSL,
                "const idCol = op.fragmentIdCol('id'); " +
                    "op.fromView('sparkTest', 'allTypes', '', idCol)" +
                    ".where(op.sqlCondition('intValue = 1'))" +
                    ".joinDoc('doc', idCol)" +
                    ".select('doc')")
            .option(Options.READ_NUM_PARTITIONS, 1)
            .option(Options.READ_BATCH_SIZE, 0)
            .load()
            .collectAsList();

        assertEquals(1, rows.size());

        Row row = rows.get(0);
        JsonNode doc = new ObjectMapper().readTree(row.getString(0));
        assertEquals(1, doc.get("allTypes").get(0).get("intValue").asInt(),
            "Verifying that the doc was correctly returned as a string in the Spark row, and could then be read via " +
                "Jackson into a JsonNode");
    }
}
