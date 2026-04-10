// swift-tools-version: 5.9
import PackageDescription

let package = Package(
    name: "Aria2server",
    platforms: [.iOS(.v15)],
    products: [
        .library(
            name: "Aria2server",
            targets: ["Aria2Plugin"])
    ],
    dependencies: [
        .package(url: "https://github.com/ionic-team/capacitor-swift-pm.git", from: "8.0.0")
    ],
    targets: [
        .target(
            name: "Aria2Plugin",
            dependencies: [
                .product(name: "Capacitor", package: "capacitor-swift-pm"),
                .product(name: "Cordova", package: "capacitor-swift-pm")
            ],
            path: "ios/Sources/Aria2Plugin"),
        .testTarget(
            name: "Aria2PluginTests",
            dependencies: ["Aria2Plugin"],
            path: "ios/Tests/Aria2PluginTests")
    ]
)