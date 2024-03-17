package edu.java.scrapper.model.mappers;

import edu.java.scrapper.model.ControllerDto.LinkResponse;
import edu.java.scrapper.model.domainDto.Link;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(
    componentModel = MappingConstants.ComponentModel.SPRING,
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)public interface LinkMapper {
    @Mapping(target = "id", source = "link.linkId")
    LinkResponse linkToLinkResponse(Link link);
}
