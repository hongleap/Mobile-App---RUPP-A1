rootProject.name = "microservices"

include(":shared-models")
include(":product-service")
include(":order-service")
include(":notification-service")
include(":api-gateway")

project(":shared-models").projectDir = file("shared-models")
project(":product-service").projectDir = file("product-service")
project(":order-service").projectDir = file("order-service")
project(":notification-service").projectDir = file("notification-service")
project(":api-gateway").projectDir = file("api-gateway")

