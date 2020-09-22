import 'package:flutter/services.dart';

class FlutterBugly {
  static String flutterMethodName = "Flutter-Upgrade-MethodChannel";
  static MethodChannel channel = MethodChannel(flutterMethodName);

  static Future<void> postException({
    String type,
    String error,
    String stackTrace,
    Map<String, String> extraInfo,
  }) async {
    try {
      await channel.invokeMethod("postException", {
        "type": type,
        "error": error,
        "stackTrace": stackTrace,
        "extraInfo": extraInfo,
      });
    } catch (e) {
      throw e;
    }
  }
}
