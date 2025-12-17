/*
 * Copyright 2025 Karlsruhe Institute of Technology.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package edu.kit.datamanager.repo.configuration;

import org.javers.core.Javers;
import org.javers.core.JaversBuilder;
import org.javers.repository.sql.ConnectionProvider;
import org.javers.repository.sql.JaversSqlRepository;
import org.javers.repository.sql.SqlRepositoryBuilder;
import org.javers.repository.sql.DialectName;
import org.springframework.context.annotation.*;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DataSourceUtils;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import javax.sql.DataSource;

@Configuration
public class JaversConfig {

  private final Environment env;
  private final DataSource dataSource;

  public JaversConfig(Environment env, DataSource dataSource) {
    this.env = env;
    this.dataSource = dataSource;
  }

  // optional: für Domänenzugriffe, NICHT für ConnectionProvider nötig
  @PersistenceContext
  private EntityManager entityManager;

  @Bean
  public Javers javers() {
    // Dialekt aus URL ableiten
    String url = env.getProperty("spring.datasource.url", "");
    DialectName dialect = (url != null && url.startsWith("jdbc:h2:"))
            ? DialectName.H2
            : DialectName.POSTGRES;

    // Spring-freundlicher ConnectionProvider
    ConnectionProvider springTxConnectionProvider = () ->
            DataSourceUtils.getConnection(dataSource);

    JaversSqlRepository sqlRepo = SqlRepositoryBuilder.sqlRepository()
            .withConnectionProvider(springTxConnectionProvider)
            .withDialect(dialect)                   // WICHTIG: Dialekt explizit setzen
            .withSchema(dialect == DialectName.H2 ? "PUBLIC" : "public")
            // .withSchemaManagementEnabled(false)   // siehe Option B unten
            .build();

    // JaVers-Instanz
    return JaversBuilder.javers()
            .registerJaversRepository(sqlRepo)
            .build();
  }
}
