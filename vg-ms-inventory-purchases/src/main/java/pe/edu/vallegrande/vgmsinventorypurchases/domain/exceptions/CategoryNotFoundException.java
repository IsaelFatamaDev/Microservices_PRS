package pe.edu.vallegrande.vgmsinventorypurchases.domain.exceptions;

public class CategoryNotFoundException extends NotFoundException {
    public CategoryNotFoundException(String id) {
        super("ProductCategory", id);
    }
}
