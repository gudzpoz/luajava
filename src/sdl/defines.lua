-- Function definitions which were not output by
-- the C preprocessor

local sdl

local function registerdefines(sdl)

   -- audio

   function sdl.AUDIO_BITSIZE(x)
      return bit.band(x, sdl.AUDIO_MASK_BITSIZE)
   end

   function sdl.AUDIO_ISFLOAT(x)
      return bit.band(x, sdl.AUDIO_MASK_DATATYPE) ~= 0
   end

   function sdl.AUDIO_ISBIGENDIAN(x)
      return bit.band(x, sdl.AUDIO_MASK_ENDIAN) ~= 0
   end

   function sdl.AUDIO_ISSIGNED(x)
      return bit.band(x, sdl.AUDIO_MASK_SIGNED) ~= 0
   end

   function sdl.AUDIO_ISINT(x)
      return not sdl.AUDIO_ISFLOAT(x)
   end

   function sdl.AUDIO_ISLITTLEENDIAN(x)
      return not sdl.AUDIO_ISBIGENDIAN(x)
   end

   function sdl.AUDIO_ISUNSIGNED(x)
      return not sdl.AUDIO_ISSIGNED(x)
   end

   function sdl.loadWAV(file, spec, audio_buf, audio_len)
      return sdl.loadWAV_RW(sdl.RWFromFile(file, "rb"), 1, spec, audio_buf, audio_len)
   end

   -- surface
   sdl.blitSurface = sdl.upperBlit

   function sdl.MUSTLOCK(S)
      return bit.band(S.flags, sdl.RLEACCEL)
   end

   function sdl.loadBMP(file)
      return sdl.loadBMP_RW(sdl.RWFromFile(file, 'rb'), 1)
   end

   function sdl.saveBMP(surface, file)
      return sdl.saveBMP_RW(surface, sdl.RWFromFile(file, 'wb'), 1)
   end
end

return registerdefines
