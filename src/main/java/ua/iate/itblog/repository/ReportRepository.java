package ua.iate.itblog.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import ua.iate.itblog.model.report.Report;

public interface ReportRepository extends MongoRepository<Report, String> {
}