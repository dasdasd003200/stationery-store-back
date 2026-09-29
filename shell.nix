{ pkgs ? import <nixpkgs> {} }:

pkgs.mkShell {
  packages = with pkgs; [
    jdk21
    maven
  ];

  shellHook = ''
    echo "☕ Spring Boot environment ready"
    echo "Java: $(java --version)"
    echo "Maven: $(mvn --version)"
    echo "para correr es : mvn spring-boot:run "
  '';
}
