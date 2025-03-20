# Role-Based UI Control Template

This document provides a template for implementing role-based conditional UI elements across the application.

## Step 1: Import the RoleAuthService

```typescript
// In your component
import { RoleAuthorizationService } from '../services/RoleAuthorization/role-authorization.service';

@Component({...})
export class YourComponent {
  constructor(
    // Other dependencies...
    public roleAuth: RoleAuthorizationService
  ) {}
}
```

## Step 2: Use in Templates

```html
<!-- Show/hide based on edit permissions -->
@if (roleAuth.canEditContent()) {
  <button (click)="editSomething()">Edit</button>
}

<!-- Show/hide based on add permissions -->
@if (roleAuth.canAddContent()) {
  <button (click)="addSomething()">Add New</button>
}

<!-- For custom permission checks -->
@if (roleAuth.hasPermission(['ADMIN', 'Support'])) {
  <div class="admin-panel">
    <!-- Admin-only content -->
  </div>
}
```

## Step 3: Adding New Permission Types

If you need to add new permission types, extend the RoleAuthService:

```typescript
// In role-authorization.service.ts
canPerformSpecialAction(): boolean {
  return this.hasPermission(['ADMIN', 'Support']);
}
```

Then use it in your templates:

```html
@if (roleAuth.canPerformSpecialAction()) {
  <button (click)="performSpecialAction()">Special Action</button>
}
```
