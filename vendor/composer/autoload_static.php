<?php

// autoload_static.php @generated by Composer

namespace Composer\Autoload;

class ComposerStaticInit156f6cbccaa3db7304741f590f8bb33f
{
    public static $prefixLengthsPsr4 = array (
        'P' => 
        array (
            'PHPMailer\\PHPMailer\\' => 20,
        ),
    );

    public static $prefixDirsPsr4 = array (
        'PHPMailer\\PHPMailer\\' => 
        array (
            0 => __DIR__ . '/..' . '/phpmailer/phpmailer/src',
        ),
    );

    public static $classMap = array (
        'Composer\\InstalledVersions' => __DIR__ . '/..' . '/composer/InstalledVersions.php',
    );

    public static function getInitializer(ClassLoader $loader)
    {
        return \Closure::bind(function () use ($loader) {
            $loader->prefixLengthsPsr4 = ComposerStaticInit156f6cbccaa3db7304741f590f8bb33f::$prefixLengthsPsr4;
            $loader->prefixDirsPsr4 = ComposerStaticInit156f6cbccaa3db7304741f590f8bb33f::$prefixDirsPsr4;
            $loader->classMap = ComposerStaticInit156f6cbccaa3db7304741f590f8bb33f::$classMap;

        }, null, ClassLoader::class);
    }
}
