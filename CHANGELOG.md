# Changelog

## [1.0.0] - 2024-02-14

### Added
- Initial release of DbBundle
- Database-backed ResourceBundle implementation
- Caffeine cache integration for high performance
- `@ValoraBundle` annotation for dependency injection
- JSF EL expression support: `#{msg['key']}`
- Spring Boot auto-configuration
- Runtime locale changing support
- MessageRepository interface for custom implementations
- DbBundleService for programmatic access
- MessageELResolver for JSF integration
- Configurable cache settings (max-size, expire-minutes)
- Cache management methods (clear, stats)
- Support for multiple locales
- Thread-safe implementation
- Comprehensive documentation and examples

### Features
- ✅ Multi-language support via database
- ✅ Compatible with Spring Boot 2.7+
- ✅ Compatible with JSF 2.3+
- ✅ Caffeine cache for performance
- ✅ Easy integration with existing projects
- ✅ Minimal configuration required
- ✅ Production-ready code

### Documentation
- README.md with comprehensive usage guide
- INSTALLATION.md with step-by-step setup
- EXAMPLES.md with real-world usage scenarios
- Inline JavaDoc comments

### Dependencies
- Spring Boot 2.7.14
- Caffeine Cache 3.1.8
- JSF API 2.3
- JPA/Hibernate
- SLF4J for logging

## Upcoming Features

### [1.1.0] - Planned
- [ ] Multiple database table support
- [ ] Message interpolation with parameters
- [ ] Export/Import functionality for messages
- [ ] Admin UI for message management
- [ ] Message versioning support
- [ ] Fallback locale support
- [ ] Message validation
- [ ] REST API for message CRUD operations

### [1.2.0] - Planned
- [ ] Redis cache option
- [ ] Message search functionality
- [ ] Bulk update operations
- [ ] Message translation suggestions
- [ ] Audit logging
- [ ] Performance metrics
- [ ] Spring Boot 3 support

## Known Issues

None currently reported.

## Migration Guide

N/A - This is the initial release.

## Contributors

- Valora Development Team

## Support

For issues, questions, or contributions, please visit the project repository.
