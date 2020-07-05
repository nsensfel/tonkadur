package tonkadur.fate.v1.error;

class ErrorCategory extends tonkadur.error.ErrorCategory
{
   public static final ErrorCategory CONFLICTING;
   public static final ErrorCategory DUPLICATE_DECLARATION;
   public static final ErrorCategory INCOMPARABLE;
   public static final ErrorCategory INCOMPATIBLE;
   public static final ErrorCategory INVALID_USE;
   public static final ErrorCategory MISSING_DECLARATION;
   public static final ErrorCategory UNKNOWN;

   static
   {
      CONFLICTING = new ErrorCategory("conflicting");
      DUPLICATE_DECLARATION = new ErrorCategory("duplicate_declaration");
      INCOMPARABLE = new ErrorCategory("incomparable");
      INCOMPATIBLE = new ErrorCategory("incompatible");
      INVALID_USE = new ErrorCategory("invalid_use");
      MISSING_DECLARATION = new ErrorCategory("missing_declaration");
      UNKNOWN = new ErrorCategory("unknown");
   }

   private ErrorCategory (final String name)
   {
      super(name);
   }
}
