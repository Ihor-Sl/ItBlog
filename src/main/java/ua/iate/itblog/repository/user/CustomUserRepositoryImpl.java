package ua.iate.itblog.repository.user;

import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;
import ua.iate.itblog.model.user.UserSearchRequest;
import ua.iate.itblog.model.user.User;

import java.time.LocalDate;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class CustomUserRepositoryImpl implements CustomUserRepository {

    private final MongoTemplate mongoTemplate;

    @Override
    public Page<User> findAll(UserSearchRequest userSearchRequest, Pageable pageable) {

        Criteria criteria = new Criteria();

        if (StringUtils.isNotBlank(userSearchRequest.getUsername())) {
            criteria.and(User.Fields.username).regex(userSearchRequest.getUsername(), "i");
        }

        LocalDate now = LocalDate.now();

        if (userSearchRequest.getFromAge() != null || userSearchRequest.getToAge() != null) {
            Criteria ageCriteria = Criteria.where(User.Fields.dateOfBirth);
            if (userSearchRequest.getFromAge() != null) {
                ageCriteria.lte(now.minusYears(userSearchRequest.getFromAge()));
            }
            if (userSearchRequest.getToAge() != null) {
                ageCriteria.gte(now
                        .minusYears(userSearchRequest.getToAge() + 1)
                        .plusDays(1));
            }
            criteria.andOperator(ageCriteria);
        }

        if (CollectionUtils.isNotEmpty(userSearchRequest.getLocations())) {
            criteria.and(User.Fields.location)
                    .in(userSearchRequest.getLocations().stream().filter(StringUtils::isNotBlank).toList());
        }

        if (CollectionUtils.isNotEmpty(userSearchRequest.getTechnologyStack())) {
            criteria.and(User.Fields.technologyStack)
                    .all(userSearchRequest.getTechnologyStack().stream().filter(StringUtils::isNotBlank).toList());
        }

        List<User> users = mongoTemplate.find(new Query(criteria).with(pageable), User.class);

        return PageableExecutionUtils.getPage(
                users,
                pageable,
                () -> mongoTemplate.count(new Query(criteria), User.class)
        );
    }
}
