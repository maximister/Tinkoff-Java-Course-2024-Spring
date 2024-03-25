package edu.java.scrapper.model.mappers;

import edu.java.scrapper.model.ControllerDto.LinkResponse;
import edu.java.scrapper.model.domainDto.Link;
import edu.java.scrapper.repository.jpa.entity.LinkEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(
    componentModel = MappingConstants.ComponentModel.SPRING,
    unmappedTargetPolicy = ReportingPolicy.IGNORE
) public interface LinkMapper {
    @Mapping(target = "id", source = "link.linkId")
    LinkResponse linkToLinkResponse(Link link);

    @Mapping(target = "id", source = "link.linkId")
    @Mapping(target = "url", expression = "java(java.net.URI.create(link.getUrl()))")
    LinkResponse linkEntityToLinkResponse(LinkEntity link);

    @Mapping(target = "url", expression = "java(java.net.URI.create(link.getUrl()))")
    Link linkEntityToLink(LinkEntity link);
}
